conDiagAndroid

Cette application assure :
-La découverte des STB via le protocole DIAL.
-La connexion au STB à condition de s'authentifier.
-Le contrôle de la STB (Télécommande, lancement des services distants).
-L'affichage de données du menu diagnostic envoyé par la STB en WARM accès.
-Les STB consultées seront enregistrés dans la base de données locale. 


Le code source est réparti selon des packages: 
"Model" encapsule les classes Server et Diagnostic sur lesquels se base notre application.
"adapter" représente l'ensemble des classes permettant l'affichage de données. 
"Sqlite" contient les classes responsables sur le stockage de la base de donnees locale. 
"Login" encapsule les classes responsable sur la phase d'authentification. 
"network" encapusle toutes les classes qui assurent les configuration et l'échange d'informations entre STB et smarphone.
"controlDiagnostic" contient le code source associée a la phase de controle et diagnostic. 
"discovery" contient les classes associés au protocole DIAL. permettant la phase de la découverte des serveurs DIAL. 
"utils" encapsule les configurations ,les constantes et les traces.
"settings"

Les bibliothèques principales qu'on a utilisé pour la transmission de données sont :
RETROFIT 2 
VOLLEY






