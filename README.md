Poubelle Connétable
===================

Une appli qui rend vos poubelles connectables.

## Enoncé de l'exercice
### Surveiller le volume de ses déchets

Implémenter un Web service qui permet à un utilisateur de suivre l’évolution de sa production de déchets.
Le domaine de l’application est le suivant : chaque utilisateur a une ou plusieurs poubelles et un historique de production de déchets. Une poubelle a un volume moyen.
Les fonctionnalités suivantes sont attendues : un utilisateur doit pouvoir s’enregistrer, s’identifier, indiquer le volume moyen de sa poubelle, déclarer quand il vide sa poubelle, consulter l’évolution de son volume de déchets au cours du temps.

### Règles à suivre :  
* Il ne doit pas être possible, pour un utilisateur, d’accéder à (ni modifier) une information concernant un autre utilisateur ;  
* L’état de l’application doit être persistant (si l’on éteint puis rallume l’application, elle doit retrouver son état avant extinction) ;  
* Toutes les actions doivent pouvoir être réalisées depuis des points d’entrée HTTP dont les entités sont au format JSON ;  
* Le modèle du domaine métier ne doit pas permettre de représenter des états invalides ;  
* Dans la mesure du possible, tous les traitements doivent être réalisés de façon non bloquante, et utiliser au mieux la capacités de calcul des machines (e.g. multi­cœurs) ;  
* Le système doit informer différemment l’utilisateur des échecs selon que ceux­ci sont dus à une erreur de la part de l’utilisateur ou à un problème technique interne.

### Bonus :
* Un point d’entrée retournant un graphique (image) montrant l’évolution du volume de déchets au cours du temps ;  
* L’API HTTP est découvrable ;  
* Une interface Web permet d’utiliser l’application depuis un navigateur Web ;  
* Le serveur HTTP est scalable horizontalement (le fait de démarrer plusieurs instances en parallèle
ne pose pas de problème).

Le plus important lors de votre travail est la maîtrise de ce que vous allez faire.  
C'est la raison pour laquelle vous avez libre choix sur les technologies, c'est un exercice à effectuer sur GitHub.

## Tester l'API en ligne
L'API est déployée sur [Google Cloud Endpoint][2], vous pouvez la tester via l'api Explorer : http://poubelle-connetable.appspot.com/_ah/api/explorer

Il faut disposer d'un compte Google pour utiliser l'API.

Le front utilise AngularJS + [Angular-google-gapi][3]  
Version consultable ici : http://poubelle-connetable.appspot.com/

## Tester l'API en local
1. mvn appengine:devserver  
2. Ouvrir chrome en mode [unsafe][1] : Chrome --user-data-dir=test --unsafeltreat-insecure-origin-as-secure=http://localhost:8080,http://0.0.0.0:8080  
3. Acceder à http://localhost:8080/_ah/api/explorer  

[1]: https://developers.google.com/explorer-help/#hitting_local_api  
[2]: https://cloud.google.com/endpoints/
[3]: https://github.com/maximepvrt/angular-google-gapi