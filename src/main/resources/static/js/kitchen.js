let allOrder
let cookingStatus
let orderStatus


function connect() {
    var socket = new SockJS('/orderme-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/app/order/doing', function (message) {
            allOrder = JSON.parse(message.body)
            initializeOrder(allOrder);
        },{ id: 2});
        stompClient.subscribe('/topic/order/update', function (message) {
            // console.log('success')
            // updateOrder(JSON.parse(message.body))
        });
    });
}

$( document ).ready(function () {
    connect()
})

function initializeOrder(tables){
    console.log(tables)
}


