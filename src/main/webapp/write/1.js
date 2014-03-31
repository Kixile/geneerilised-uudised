$.getJSON("../articles/article.json",{}, function( data ){ 
  document.getElementById("header").innerHTML=data.header;
  document.getElementById("authortime").innerHTML="By " + data.author + " on " + data.time;
  document.getElementById("image").src=data.image;
  document.getElementById("text").innerHTML=data.body;
  for (i=0;i<data.tags.length;i++){
	document.getElementById('tags').innerHTML += '<a>' + data.tags[i] + '</a>';
	}
});