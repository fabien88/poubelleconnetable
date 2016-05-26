var controller = angular.module('angular-google-api-example.controller.home', []);

controller.controller('angular-google-api-example.controller.home', ['$scope', 'GApi',
    function homeCtl($scope, GApi) {

        var graphData;
        var trashToIndex = {};
        var chart;
        var options = {
            title: 'Waste volume curve',
            curveType: 'function',
            legend: {position: 'bottom'},
        };

        GApi.executeAuth('trashManager', 'trashEndpoint.listTrash').then(function (resp) {
            $scope.trashes = resp.items;
        });

        GApi.executeAuth('trashManager', 'trashEndpoint.listTrashStatistics').then(function (resp) {
            if (typeof(resp.items) !== 'undefined') {
                // Create the data table.
                graphData = new google.visualization.DataTable();
                graphData.addColumn('datetime', 'Date');

                var formattedData = [];

                var lineAdded = 0;
                for (var i = 0, l = resp.items.length; i < l; ++i) {
                    var trash = resp.items[i].trash;
                    var emptyDates = resp.items[i].emptyDates;

                    graphData.addColumn('number', resp.items[i].trash.name);
                    trashToIndex[resp.items[i].trash.name] = i + 1;

                    if (typeof(emptyDates) !== 'undefined') {
                        var trashEmptyCount = 0;
                        for (var j = 0, lj = emptyDates.length; j < lj; ++j) {
                            formattedData.push({
                                date: new Date(emptyDates[j]),
                                trashIndex: i + 1,
                                trashVolume: trash.volume
                            });
                        }
                    }
                }
                if (formattedData.length > 0) {
                    // We want array sorted on date
                    formattedData.sort(function (a, b) {
                        a = new Date(a.date);
                        b = new Date(b.date);
                        return a > b ? 1 : a < b ? -1 : 0;
                    });

                    graphData.addRow();
                    graphData.setCell(0, 0, formattedData[0].date);
                    for (var j = 0, lj = resp.items.length; j < lj; ++j) {
                        graphData.setCell(0, j + 1, 0);
                    }
                    graphData.setCell(0, formattedData[0].trashIndex, formattedData[0].trashVolume);
                    for (var i = 1, l = formattedData.length; i < l; ++i) {
                        graphData.addRow();

                        // init line with previous line data
                        for (var j = 0, lj = resp.items.length; j < lj; ++j) {
                            graphData.setCell(i, j + 1, graphData.getValue(i - 1, j + 1));
                        }
                        graphData.setCell(i, 0, formattedData[i].date);
                        graphData.setCell(i, formattedData[i].trashIndex, graphData.getValue(i - 1, formattedData[i].trashIndex) + formattedData[i].trashVolume);
                    }


                    // Instantiate and draw our chart, passing in some options.
                    // var view = new google.visualization.DataView(graphData);
                    // view.setColumns([1, {calc: cmToInches, type: 'number', label: 'Total'}]);
                    // function cmToInches(dataTable, rowNum) {
                    //     var sum = 0;
                    //     for (var i = 0, l = trashToIndex.length; i < l; ++i) {
                    //         sum += dataTable.getValue(rowNum, i + 2);
                    //     }
                    //     return sum;
                    // }

                    chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                    chart.draw(graphData, options);
                }
            }
        });

        $scope.delete = function (trash) {
            GApi.executeAuth('trashManager', 'trashEndpoint.deleteTrash', {'trashName': trash.name}).then(function (resp) {
                for (var i = 0; i < $scope.trashes.length; i++) {
                    if ($scope.trashes[i]['name'] == trash.name) {
                        if (i > -1) {
                            $scope.trashes.splice(i--, 1);
                        }
                    }
                }
                for (var i = 0; i < graphData.getNumberOfRows(); ++i) {
                    graphData.setCell(i, trashToIndex[trash.name], 0);
                }
                chart.draw(graphData, options);

            });
        };
        $scope.empty = function (trash) {
            GApi.executeAuth('trashManager', 'trashEndpoint.emptyTrash', {'trashName': trash.name}).then(function (resp) {
                trash.show = true;
                $scope.remove = function (item) {
                    trash.show = false;
                }

                // update graphic
                graphData.addRow();
                var lineIdx = graphData.getNumberOfRows() - 1;
                var previousVolume = 0;
                if (lineIdx > 0) {
                    previousVolume = graphData.getValue(lineIdx - 1, trashToIndex[trash.name]);
                    for (var j = 0, lj = Object.keys(trashToIndex).length; j < lj; ++j) {
                        graphData.setCell(
                            lineIdx,
                            j + 1,
                            graphData.getValue(lineIdx - 1, j + 1));
                    }
                } else {
                    graphData = new google.visualization.DataTable();
                    graphData.addRow();
                    graphData.addColumn('datetime', 'Date');
                    graphData.addColumn('number', trash.name);
                    trashToIndex[trash.name] = 1;
                    chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                }

                graphData.setCell(lineIdx, 0, new Date());
                graphData.setCell(lineIdx, trashToIndex[trash.name], trash.volume + previousVolume);
                chart.draw(graphData, options);

            });
        };
    }
]);