
// RTFM:
// http://javascript.crockford.com/private.html
// http://blog.stannard.net.au/2011/01/14/creating-namespaces-in-javascript/

var comments = comments || {};

comments.getCommentsById = function(bids, id) {
    return $.grep(bids, function(elem) {
       return elem.id == id;
    });
};

comments.BrowsingList = function(jList, viewingList) {
    this.list = jList;
    this.view = viewingList;
    this.items = {};
};

comments.BrowsingList.prototype.addComment = function(item) {
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



comments.ViewingList = function(jViewPanel) {
    this.view = jViewPanel;
    this.descriptionParagraph = $('#itemdescription', jViewPanel);
    this.bidsDiv = $('#bids', jViewPanel);
    this.selectedItem = null;
};

comments.ViewingList.prototype.setViewContent = function(commentToDisplay) {
    this.selectedItem = commentToDisplay;
    this.descriptionParagraph.html(
        commentToDisplay.title + ": <br/>" + commentToDisplay.description);

    this.clearBids();
    for (var i = 0; i < commentToDisplay.bids.length; i++) {
        this.addBid(commentToDisplay.bids[i]);
    }
	
	document.getElementById('mycomment').className = "";
	document.getElementById('items').className = "hidden";
    //this.view.removeClass('hidden');
};

comments.ViewingList.prototype.clearBids = function() {
    this.bidsDiv.empty();
};

comments.ViewingList.prototype.canAdd = function(comment) {
    return this.selectedItem && this.selectedItem.id == comment.itemId;
};

comments.ViewingList.prototype.addBid = function(comment) {
    if (!this.canAdd(comment)) {
        return;
    }
	
    var commentElement = $('<li/>');
	var commentElement2 = $('<div/>');
	var commentElement3 = $('<p/>');
	var commentElement4 = $('<p/>');
	commentElement2.append(commentElement3.text('By ' + comment.bidder));
	commentElement2.append(commentElement4.text(comment.amount));
	commentElement.append(commentElement2);
	console.log(commentElement);
    this.bidsDiv.append(commentElement);
};



comments.CommentBuilder = function(jInputSection) {
    this.aliasBox = $('#alias', jInputSection);
    this.commentBox = $('#comment', jInputSection);
};

comments.CommentBuilder.prototype.clear = function() {
    this.aliasBox.val('');
    this.commentBox.val('');
};

comments.CommentBuilder.prototype.buildBid = function(item) {
    return {
        itemId: item.id,
        bidder: this.aliasBox.val(),
        amount: this.commentBox.val()
    };
};
