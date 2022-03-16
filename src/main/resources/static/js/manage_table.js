let stompClient = null;

let numberTable = [1,2,3]
let available = [true,true,true]



$( document ).ready((() => {
    connect()
    createTableCard()
}))

function connect() {
    var socket = new SockJS('/orderme-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/app/table/all', function (message) {
            console.log(message);
        },{ id: 1});
        stompClient.unsubscribe(1)
        stompClient.subscribe('/topic/table/update', function (message) {
            console.log(message);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}



function createTableCard(){
    for(let i=0;i< numberTable.length;i++){
        let card = document.createElement('div');
        card.className = 'card text-center col';

        let cardBody = document.createElement('div');
        cardBody.className = 'card-body';

        let cardTitle = document.createElement('h5');
        cardTitle.className = 'card-title';
        cardTitle.innerText = numberTable[i]

        if(!available[i]){
            card.classList.add("d-none")
        }

        cardBody.appendChild(cardTitle)
        card.appendChild(cardBody)
        $('#tableContainer').append(card)
    }
}