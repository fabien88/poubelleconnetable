var controller = angular.module('angular-google-api-example.controller.home', []);

controller.controller('angular-google-api-example.controller.home', ['$scope', 'GApi',
    function homeCtl($scope, GApi) {

        GApi.executeAuth('trashManager', 'trashEndpoint.listTrash').then(function (resp) {
            $scope.trashes = resp.items;
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
            });
        };
        $scope.empty = function (trash) {
            GApi.executeAuth('trashManager', 'trashEndpoint.emptyTrash', {'trashName': trash.name}).then(function (resp) {
                trash.show = true;
                $scope.remove = function (item) {
                    trash.show = false;
                }
            });
        };
    }
]);