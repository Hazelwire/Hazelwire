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
		<script type="text/javascript" src="js/collapse.js"></script>
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
                                  legend:{show:true},
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
                            });
                            function changeSort(){
                                location="setlang.asp?lang="$('#winsort').val();
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
							<li><div>Team</div>
								<div>points</div>
							</li>
						</ul>
						<ol class="scorelist">
                                                        {foreach from=$contestants item=contestant}
                                                            <li>
                                                                    <div>{$contestant->getTeamname()}</div>
                                                                    <div>{$contestant->getPoints()}</div>
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
							<input id="flagsubmit" name="sub_flag" type="submit" value="Claim Flag" tabindex="2"></input>
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
								<input type="radio" name="announcement" id="announcement{$announcement->id}"/>
								<label for="announcement{$announcement->id}">{$announcement->title}
									<div>{$announcement->content}</div>
								</label>
							</li>
                                                        {/foreach}
						</ul>
					</div>
				</div>
			</div>
			<div id="timeline">
				<div id="chart1" class="content" style="height:auto;width:auto;">
					
				</div>
			</div>
		</div>
	</body>
</html>
