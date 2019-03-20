PATH_TO_APK="app/build/outputs/apk/release/app-release.apk"
PATH_TO_RELEASE_NOTES="release_notes.txt"

 TARGET_GROUP="UltimateGroup"

sudo npm install -g appcenter-cli

appcenter login --disable-telemetry --token ${TOKEN}

appcenter distribute release --disable-telemetry \
    -f $PATH_TO_APK \
    -g $TARGET_GROUP \
    --app yks72p/TrafimauApp  \
    -R $PATH_TO_RELEASE_NOTES

