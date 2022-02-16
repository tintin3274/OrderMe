let item = []
let i

$.get( '/api/item', function( data ) {
    item = data;
    createCard()
});

function createCard(){
    console.log(item)
    for(i=0;i<item.length;i++){
        let card = document.createElement('div');
        card.className = 'col-md-6 card d-flex';

        let detail = document.createElement('div');
        detail.className ='row my-1'

        let pictureFrame =document.createElement('div');
        pictureFrame.className = 'columnImage col-auto'

        var picture = document.createElement('img');
        picture.className = 'picture';
        picture.src = '/images/'+item[i].image
        picture.onerror = errorPicture(picture)

        let cardBody = document.createElement('div');
        cardBody.className = 'col-7 card-body'

        let title = document.createElement('h5');
        title.innerText = item[i].name;
        title.className = 'card-title';

        let cardText = document.createElement('p');
        cardText.className = 'card-text'

        let text = document.createElement('small')
        text.className = 'text-muted hide-text'
        text.innerText = item[i].description

        let link = document.createElement('a')
        link.className = 'stretched-link'
        link.href = '/admin/create-item'

        pictureFrame.appendChild(picture)
        cardBody.appendChild(title)
        cardText.appendChild(text)
        cardBody.appendChild(cardText)
        detail.appendChild(pictureFrame)
        detail.appendChild(cardBody)
        card.appendChild(detail)
        $('#cardContainer').append(card)
    }
}

function errorPicture(picture){
    picture.src = '/images/default.png'
}

