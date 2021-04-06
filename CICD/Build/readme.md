docker login bsir2465.azurecr.io -u bsir2465 -p pass

docker tag thesisapi:local bsir2465.azurecr.io/thesisapi:local

docker push bsir2465.azurecr.io/thesisapi:local

docker build -f .\docker\Dockerfile -t aaatest .


az acr repository list -n bsir2465
