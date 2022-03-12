let numberTable = [1,2,3]
let available = [true,true,true]



$( document ).ready((() => {
    createTableCard()
}))

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