
var vldemo2 = {
    loadAuctionItems: function(itemList) {
        $.ajax('/auctions', {
            dataType: 'json',
            success: function (itemsJson) {
                console.info('ajax success');
                for (var i = 0; i < itemsJson.length; i++) {
                    itemList.addItem(itemsJson[i]);
                }
            },
            error: function (req, text) {
                console.error('failed to load auction items: ' + text);
            }
        });
    },

    makeBid: function(view, bidBuilder) {
        var item = view.selectedItem;
        var bid = bidBuilder.buildBid(item);
        $.ajax('/auctions', {
            type: 'POST',
            data: JSON.stringify(bid), // pack the bid object into json string
            success: function(savedBid) {
                // server returns the bid with its new generated id
                // syncing js&dom is a pain. angularjs may help
                bidBuilder.clear();
                if (!auctions.getBidsById(item.bids, savedBid.id).length) {
                    item.bids.push(savedBid);
                    view.setViewContent(item);
                }
            },
            error: function(req, text) {
                console.error('failed to post bid: ' + text);
            }
        });
    },

    createWebsocket: function(allItems, view) {
        var socketAddr = window.location.origin.replace("http", "ws") + "/feed";
        var websocket = new WebSocket(socketAddr);
        websocket.onopen = function() { console.log("socket up!"); };
        websocket.onclose = function() { console.log("socket closed!"); };
        websocket.onmessage = function(evt) {
            console.log("ws received " + evt.data);
            var bid = JSON.parse(evt.data);
            var item = allItems[bid.itemId];
            if (!auctions.getBidsById(item.bids, bid.id).length) {
                item.bids.push(bid);
                if (view.selectedItem.id == item.id) {
                    view.setViewContent(item);
                }
            }
        };
    }
};

$(function() {

    console.log("running demoapp.js");

    var bidBuilder = new auctions.BidBuilder($('#mycomment'));
    var viewingList = new auctions.ViewingList($('#viewing'));
    var itemList = new auctions.BrowsingList($('#items'), viewingList);

    $('#makepost').click(function() {
        vldemo2.makeBid(viewingList, bidBuilder);
    });

    vldemo2.loadAuctionItems(itemList);
    vldemo2.createWebsocket(itemList.items, viewingList);

});