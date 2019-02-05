TOKEN="TESTING"

UPLOADS_URL='https://appcenter.ms/users/yks72p/apps/TrafimauApp/release_uploads'
HEADER="X-API-Token: $TOKEN"

echo $HEADER

# curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header $HEADER $UPLOADS_URL

