var app = angular.module('angular-google-api-example', [
    'ngCookies',
    'ngMessages',
    'ui.router',
    'angular-google-gapi',

    'angular-google-api-example.router',
    'angular-google-api-example.controller'

]);

app.run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window', '$cookies',
    function (GAuth, GApi, GData, $state, $rootScope, $window, $cookies) {

        $rootScope.gdata = GData;

        var CLIENT = '599643511493-otk98584efl01di4hp1rfqdgm3tkn25l.apps.googleusercontent.com';
        var BASE;
        if ($window.location.hostname == 'localhost') {
            BASE = '//localhost:8080/_ah/api';
        } else {
            BASE = 'https://poubelle-connetable.appspot.com/_ah/api';
        }

        //BASE = 'https://poubelle-connetable.appspot.com/_ah/api';

        GApi.load('trashManager', 'v1', BASE);
        GAuth.setClient(CLIENT);
        GAuth.setScope('https://www.googleapis.com/auth/userinfo.email');

        var currentUser = $cookies.get('userId');
        if (currentUser) {
            GData.setUserId(currentUser);
            GAuth.checkAuth().then(
                function () {
                    if ($state.includes('login'))
                        $state.go('home');
                },
                function () {
                    $state.go('login');
                }
            );
        } else {
            $state.go('login');
        }


        $rootScope.logout = function () {
            GAuth.logout().then(function () {
                $cookies.remove('userId');
                $state.go('login');
            });
        };
    }
]);
