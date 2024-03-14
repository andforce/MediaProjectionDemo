var ios = require('socket.io-client');

const socket_client = ios('http://localhost:8088');


socket_client.on('connect', function () {
  console.log('connect');

});
socket_client.on('event', function (data) {
  console.log(data);

});
socket_client.on('disconnect', function () {
  console.log("disconnect");
});

console.log("Start!!");
