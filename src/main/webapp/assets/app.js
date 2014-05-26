
var vldemo2 = {
    loadAuctionItems: function(commentList) {
		var str = document.URL;
		var res = str.split("/");
		//alert(res[4]);
        $.ajax('/comments/'+res[4], {
            dataType: 'json',
            success: function (itemsJson) {
                console.info('ajax success');
                for (var i = 0; i < itemsJson.length; i++) {
                    commentList.addComment(itemsJson[i]);
                }
            },
            error: function (req, text) {
                console.error('failed to commenters: ' + text);
            }
        });
    },

    makeBid: function(view, CommentBuilder) {
        var item = view.selectedItem;
        var bid = CommentBuilder.buildBid(item);
		var one = {"one" : document.getElementById('recaptcha_response_field').value};
		var two = {"two" : document.getElementById('recaptcha_challenge_field').value};
        $.ajax('/comments/', {
            type: 'POST',
            data: {json_1:JSON.stringify(bid), json_2:JSON.stringify(one), json_3:JSON.stringify(two)}, // pack the bid object into json string
            success: function(savedBid) {
                // server returns the bid with its new generated id
                // syncing js&dom is a pain. angularjs may help
                CommentBuilder.clear();
                if (!comments.getCommentsById(item.bids, savedBid.id).length) {
                    item.bids.push(savedBid);
                    view.setViewContent(item);
                }
            },
            error: function(req, text) {
                console.error('failed to comment: ' + text);
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
            if (!comments.getCommentsById(item.bids, bid.id).length) {
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

    var CommentBuilder = new comments.CommentBuilder($('#mycomment'));
    var viewingList = new comments.ViewingList($('#viewing'));
    var commentList = new comments.BrowsingList($('#items'), viewingList);

    $('#makepost').click(function() {
        vldemo2.makeBid(viewingList, CommentBuilder);
    });

    vldemo2.loadAuctionItems(commentList);
    vldemo2.createWebsocket(commentList.items, viewingList);

});

         function showRecaptcha(element) {
           Recaptcha.create("6Le37vMSAAAAAKoR2M1Bbg2RTCb-0X5rBdRaHHsk", element, {
             theme: "red",
             callback: Recaptcha.focus_response_field});
         }