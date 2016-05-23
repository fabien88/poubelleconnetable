var controller = angular.module('angular-google-api-example.controller.add', []);

controller.controller('angular-google-api-example.controller.add', ['$scope', 'GApi', '$state',
    function homeCtl($scope, GApi, $state) {
        $scope.submitAdd = function () {
            GApi.executeAuth('trashManager', 'trashEndpoint.createTrash', $scope.trash).then(function (resp) {
                $state.go('home');
            }, function (e) {
                $scope.addTrash.$error = {
                    "apiError": true,
                    "message": e.error.message
                };
            });
        }
    }
]);