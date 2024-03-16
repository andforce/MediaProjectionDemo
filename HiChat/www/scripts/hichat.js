
window.onload = function() {
    var hichat = new HiChat();
    hichat.init();
};
var HiChat = function() {
    this.socket = null;
};
HiChat.prototype = {
    init: function() {
        var that = this;
        this.socket = io.connect();
        this.socket.on('connect', function() {

        });

        this.socket.on('error', function(err) {

        });
        this.socket.on('system', function(nickName, userCount, type) {

        });

        this.socket.on('updateImage', function(user, img, color) {
            that._updateImage(user, img, color);
        });

    },

    _updateImage: function(user, pathWithTime, color) {
        const image = document.getElementById('image');
        image.src = pathWithTime;
    }
};
