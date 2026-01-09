# Instructions
## Pré-requis
**Pour lancer le projet** vous devez au préalable avoir JavaFX 21.0.10 ainsi
qu'au moins Java 18.

Ensuite aller dans le PowerShell, et taper ceci :
```bash
& "C:\Program Files\Java\jdk-25\bin\java.exe" `
>> --module-path "C:\javafx-sdk-21.0.10\lib" `
>> --add-modules javafx.controls,javafx.fxml,javafx.graphics `
>> -jar "chemin absolu au .jar"
```

Évidemment la librairie peut se trouver ailleurs, n'hésitez pas à vérifier dans vos variables
d'environnement -> PATH.

Le fichier se trouve à la source du projet : `AP4B_project.jar`