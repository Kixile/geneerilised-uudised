<!DOCTYPE html>

<head>
	#include("../templates/template-head.html")
</head>

<body class="home blog">

	<div id="header"> <!-- Header start -->
		#include("templates/template-header.html")
	</div> <!-- header end -->

	<div id="page-wrapper"> <!-- News start here -->
		<div id="page">
			<div id="content">
				<div class="content">
				
					<div id="post-prototype-001" class="block"> <!-- article example -->
						<div class="post-inner">
							<h2>Article header is here</h2>
							<div class="entry">
								<p class="info">By someone on whatever date it is.</p> <!-- here is submition info-->
								<p><img alt="" src="images/placeholder2.png" title="placeholder"></p><!-- illustrative image -->
								<p><!-- article text -->
									Lorem ipsum dolor sit amet, sapien etiam, nunc amet dolor ac odio mauris justo.
									Luctus arcu, urna praesent at id quisque ac. Arcu massa vestibulum malesuada, integer vivamus elit eu mauris eu, cum eros quis aliquam nisl wisi.
									Lorem ipsum dolor sit amet, sapien etiam, nunc amet dolor ac odio mauris justo. 
									Luctus arcu, urna praesent at id quisque ac. Arcu massa vestibulum malesuada, integer vivamus elit eu mauris eu, cum eros quis aliquam nisl wisi.
								</p>
								<p><a class="more-link" href="">>> Read more...</a></p><!-- link to the full article -->
							</div>
							<div class="hr"></div> <!-- seperator -->
							<p class="tags"><!-- tags column -->
								<a rel="" href="">Tag1</a>, 
								<a rel="" href="">Tag2</a>, 
								<a rel="" href="">Tag3</a>
							</p>
							<p class="comments">
								<a title=">> Comment on this" href="">>> Comment on this...</a>
							</p>
						</div>
					</div>
					
					<div class="navigations">
							<div class="alignleft">
								<a href="">&#60; &#60;</a>
							</div>
							<div class="alignright">
								<a href="">&#62; &#62;</a>
							</div>
							<div class="clear"></div>
					</div>
				</div>
				<div id="sidebar">
					#include("templates/template-sidebar.html")
				</div>
			</div>
		</div>
	</div>
	
	<div id="footer">
		#include("templates/template-footer.html")
	</div>
</body>