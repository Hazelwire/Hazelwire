<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>{$title} - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/contestant.css"></link>
                <link rel="stylesheet" type="text/css" href="css/jquery.jqplot.css" />
                <!--[if lt IE 9]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
                <script language="javascript" type="text/javascript" src="js/jquery.jqplot.js"></script>
                <script language="javascript" type="text/javascript" src="js/jqplot.dateAxisRenderer.js"></script>
                <script language="javascript" type="text/javascript" src="js/jqplot.cursor.js"></script>
                <script language="javascript" type="text/javascript" src="js/jqplot.highlighter.js"></script>
		<script type="text/javascript" src="js/collapse_contestant.js"></script>
                {*<script type="text/javascript" src="js/buttons.js"></script>*}
		<script type="text/javascript" src="js/timeline.js"></script>
		<script type="text/javascript" src="js/textfield.js"></script>
                <script type="text/javascript" src="js/contestant.js"></script>
                <style type="text/css" media="screen">
                 {literal}  .jqplot-axis {
                     font-size: 0.8em;
                   }{/literal}
                </style>
                     <script type="text/javascript" language="javascript">
			var plot1;
                            {literal}$(document).ready(function(){{/literal}
                            $.jqplot.config.enablePlugins = true;
                              {foreach from=$series->series item=serie}
                                var {$serie->id}={$serie->string};
                              {/foreach}

                              plot1 = $.jqplot('chart1', [{$series->seriesString}],{literal} {
                                  legend:{show:true, location:'nw'},
                                  axes:{
                                    xaxis:{
                                      renderer:$.jqplot.DateAxisRenderer,
                                              showTicks: false,
                                              min:{/literal}{math equation='x - y' x=$start_time y=60000},{literal}
                                      tickOptions:{
                                        formatString:'%r'
                                      }
                                    },
                                    yaxis:{
                                      tickOptions:{
                                        formatString:'%i'
                                        }
                                    }
                                  },
                                  highlighter: {
                                    show: true,
                                    sizeAdjust: 7.5,
                                            formatString: '%s, %s pts'
                                  },
                                  cursor: {
                                            zoom: true,
                                            showTooltip:false
                                  },
                                  series:[ {/literal}
                                    {counter start=0 skip=1 assign="count"}
                                    {foreach from=$series->series item=serie}{counter}
                                        {literal}{{/literal}showLabel:true, label:'{$serie->name|escape:'htmlall'}'{literal}}{/literal}
                                        {if $count != ($series->series|@count)},{/if}
                                    {/foreach}
                                    {literal}
                                  ]
                              });
                            function onresize() {
                                    plot1.replot();
                            };

                            var resizeTimer;
                            $(window).resize(function() {
                                clearTimeout(resizeTimer);
                                resizeTimer = setTimeout(onresize, 100);
			});
                            {/literal}{if isset($endgame)}
                                    $('#flagform').prepend("<div id=\"flagresponse\" style=\"display: none; right: 0pt; left: 0pt; bottom: 2em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);\"> The wargame has ended!</div>");{literal}
                                    $('#flagdisplay').animate({bottom: (40+$('#flagresponse').outerHeight())+'px'});
                                    $('#flagresponse').slideDown();
                                    $('ol.scorelist li').first().css("background-color","green").css("color","white");{/literal}
                            {/if}
                            {literal}});
                            function changeSort(){
                                location="index.php?winsort=" + $('#winsort').val();
                            }{/literal}
                        </script>
	</head>
	<body>
		<div id="container">
			<div id="scoreboard">
				<div class="content">
					<div class="header">
						<h1>Scoreboard</h1>
					</div>
					<div id="scorecontainer">
						<ul class="scorelist">
							<li><span>Team</span>
								<span>points</span>
							</li>
						</ul>
						<ol class="scorelist">{counter start=0 skip=1 assign="count"}
                                                        {foreach from=$contestants item=contestant}
                                                            <li>
                                                                    <div class="title-bar">
                                                                    <span id='{counter}{$count}'>{$contestant->getTeamname()}</span>
                                                                    <span>{$contestant->getPoints()}</span>
                                                                    </div>
                                                                    <div class="cextrainfo">
                                                                        {if !is_null($contestant->getImage()) && !is_null($contestant->getTagline())}
                                                                                <div class="image" style="background: #000 url('images/{$contestant->getImage()}') no-repeat right top;"> &nbsp </div>
                                                                                <div class="tagline"> {$contestant->getTagline()|escape} </div>
                                                                        {elseif $id == $contestant->getId()}
                                                                                {if $num_errors > 0}
                                                                                {foreach from=$errors item=error}
                                                                                    {if $error->getType() == "imgtag"}
                                                                                        <div id="flagresponse" style="display: block; right: 0; left: 0; bottom: 0em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);margin-bottom:2px;"> {$error->getMessage()}</div>
                                                                                    {/if}
                                                                                {/foreach}
                                                                                {/if}
                                                                                <form method="POST" action="index.php" enctype="multipart/form-data">
                                                                                    <div class="input-wrap defaultText" title="Max 500kb. JPG, GIF or PNG."> Image: <input id="fileinput" type="file" name="img"/> </div>
                                                                                    <div class="input-wrap defaultText" title="Max 140 character. No markup."> Tagline: <input type="text" name="tag" /> </div>
                                                                                    <div class="input-wrap defaultText" style="margin-bottom:5px;"> <input type="submit" value="Submit" /> </div>
                                                                                </form>
                                                                        {else}
                                                                                <div class="image" style="background: #aeaeae"> None </div>
                                                                                <div class="tagline"> None </div>
                                                                        {/if}
                                                                    </div>
                                                            </li>
                                                        {/foreach}
						</ol>
					</div>
				</div>
			</div>
			<div id="flags">
				<div class="content">
					<div class="header">
						<h1>Flags</h1>
					</div>
					<div id="flagdisplayselection">
						display
						<select id='winsort' onchange="changeSort()">
							<option {if $winsort == "flagtype"}selected{/if} value="flagtype">by Flag Type</option>
							<option {if $winsort == "warserver"}selected{/if} value="warserver">by Warserver</option>
						</select>
					</div>
					<div id="flagdisplay">
						<ul class="collapsible">
                                                        {foreach from=$flags item=flag}
                                                            <li>
                                                                    <input type="radio" name="flag" id="{$flag->id}"/>
                                                                    <label for="{$flag->id}">{$flag->name}
                                                                    {foreach from=$flag->wins item=win}
                                                                    {if not $win->submitted}
                                                                        <div class="unclaimed">{$win->name}</div>
                                                                    {else}
                                                                        <div class="claimed">{$win->name}</div>
                                                                    {/if}
                                                                    {/foreach}
                                                                    </label>
                                                            </li>
                                                         {/foreach}
						</ul>
					</div>
					<form id="flagform" action="index.php" method="POST" >
						<div id="flaginput">
							<input id="flaginputfield" title="Insert flag code here" class="defaultText" name="flagcode" type="text" tabindex="1"></input>
						</div>
						<div id="flagsubmission">
							<input id="flagsubmit" name="sub_flag" type="submit" value="Claim Flag" tabindex="2" {if isset($endgame)}disabled{/if}></input>
						</div>
					</form>
				</div>
			</div>
			<div id="announcements">
				<div class="content">
					<div class="header">
						<h1>Announcements</h1>
					</div>
					<div id="announcementdisplay">
						<ul class="collapsible">
                                                        {foreach from=$announcements item=announcement}
							<li>
								<input type="radio" name="announcement" id="announcement_{$announcement->id}"/>
								<label for="announcement_{$announcement->id}">{$announcement->title}
                                                                        <span>{$announcement->timestamp|date_format:'%e/%m/%Y @ %H:%M'}</span>
									<div>{$announcement->content}</div>
								</label>
							</li>
                                                        {/foreach}
						</ul>
					</div>
				</div>
			</div>
			<div id="timeline"><div id="chart1wrap" class="content" style="height:auto;width:auto;">
				<div id="chart1" style="left: 0pt; right: 0pt; top: 0pt; bottom: 0pt;height:auto;width:auto;">

				</div></div>
			</div>
		</div>
	</body>
</html>
