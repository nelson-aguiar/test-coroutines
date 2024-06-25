FROM nginx:latest
MAINTAINER IEU
COPY ./docker-resources/nginx/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80 443
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]