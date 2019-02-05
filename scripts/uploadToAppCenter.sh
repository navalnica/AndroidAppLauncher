PATH_TO_APK="app/build/outputs/apk/release/app-release.apk"
PATH_TO_RELEASE_NOTES="../config/release_notes.txt"

sudo npm install -g appcenter-cli

echo "context FOO: $FOO"
echo "local foo: $FOO_LOCAL"
echo "current dit: `pwd"

appcenter login --disable-telemetry --token ${TOKEN_LOCAL}

appcenter distribute release --disable-telemetry \
    -f $PATH_TO_APK \
    -g dudes \
    --app yks72p/TrafimauApp  \
    -R $PATH_TO_RELEASE_NOTES

