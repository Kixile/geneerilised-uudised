
// RTFM:
// http://javascript.crockford.com/private.html
// http://blog.stannard.net.au/2011/01/14/creating-namespaces-in-javascript/

var auctions = auctions || {};

auctions.getBidsById = function(bids, id) {
    return $.grep(bids, function(elem) {
       return elem.id == id;
    });
};

auctions.BrowsingList = function(jList, viewingList) {
    this.list = jList;
    this.view = viewingList;
    this.items = {};
};

auctions.BrowsingList.prototype.addItem = function(item) {
    var self = this;
    this.items[item.id] = item;

    var newListItem = $('<li/>');
    newListItem.text(item.title);
    newListItem.click(function() {
        // 'this' is the item that was clicked
        // 'self' is the BrowsingList object
        self.view.setViewContent(item);
    });
    this.list.append(newListItem);
};



auctions.ViewingList = function(jViewPanel) {
    this.view = jViewPanel;
    this.descriptionParagraph = $('#itemdescription', jViewPanel);
    this.bidsDiv = $('#bids', jViewPanel);
    this.selectedItem = null;
};

auctions.ViewingList.prototype.setViewContent = function(itemToDisplay) {
    this.selectedItem = itemToDisplay;
    this.descriptionParagraph.html(
        itemToDisplay.title + ": <br/>" + itemToDisplay.description);

    this.clearBids();
    for (var i = 0; i < itemToDisplay.bids.length; i++) {
        this.addBid(itemToDisplay.bids[i]);
    }
	
	document.getElementById('mycomment').className = "";
	document.getElementById('items').className = "hidden";
    //this.view.removeClass('hidden');
};

auctions.ViewingList.prototype.clearBids = function() {
    this.bidsDiv.empty();
};

auctions.ViewingList.prototype.canAdd = function(bid) {
    return this.selectedItem && this.selectedItem.id == bid.itemId;
};

auctions.ViewingList.prototype.addBid = function(bid) {
    if (!this.canAdd(bid)) {
        return;
    }
	
    var bidElement = $('<li/>');
	var bidElement2 = $('<div/>');
	var bidElement3 = $('<p/>');
	var bidElement4 = $('<p/>');
	bidElement2.append( bidElement3.text('By ' + bid.bidder));
	bidElement2.append(bidElement4.text(bid.amount));
	bidElement.append(bidElement2);
	console.log(bidElement);
    this.bidsDiv.append(bidElement);
};



auctions.BidBuilder = function(jInputSection) {
    this.aliasBox = $('#alias', jInputSection);
    this.commentBox = $('#comment', jInputSection);
};

auctions.BidBuilder.prototype.clear = function() {
    this.aliasBox.val('');
    this.commentBox.val('');
};

auctions.BidBuilder.prototype.buildBid = function(item) {
    return {
        itemId: item.id,
        bidder: this.aliasBox.val(),
        amount: this.commentBox.val()
    };
};
