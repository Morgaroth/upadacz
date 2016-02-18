# upadacz
Krótka instrukcja instalacji:
 1. Pobrać i zainstalować (jeśli nie ma) Android SDK: http://developer.android.com/sdk/index.html
 2. Po pobraniu projektu, ustawić ściężkę do Android SDK w pliku local.properties
 3. Zbudować projekt lokalnym skryptem gradle (w katalogu projektu):
  - Windows: ./gradlew.bat packageDebug
  - Unix: ./gradlew packageDebug
 4. Aby zainstalować aplikację na podłączonym telefonie, można wykonać (zamiast packageDebug lub po) ./gradlew installDebug
