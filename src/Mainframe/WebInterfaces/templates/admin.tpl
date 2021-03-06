<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>{$title} - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
                <link rel="stylesheet" type="text/css" href="css/sanityTable.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
                <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/collapse.js"></script>
                <script type="text/javascript" src="js/textfield.js"></script>
                <script type="text/javascript" src="js/admin.js"></script>
                <script type="text/javascript">
            {literal}$(document).ready(function(){
                        setTimeout("updateClistAuto()", 30000);
                     });{/literal}
                </script>
	</head>
	<body>
                <div id="popup">
			<a id="popupClose">x</a>
			<h1>Insert popup title here.</h1>
			<div id="popupcontent">Insert popup contents here.<br /><br />
				Maecenas velit nisi, ornare sed rhoncus ut, suscipit eget velit. Integer a augue nisi. Vestibulum mi elit, gravida in pharetra id, consectetur quis neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum sollicitudin suscipit tellus, vitae cursus ligula pulvinar vitae. Integer pellentesque erat nec massa laoreet quis luctus turpis lacinia. Vivamus quis odio at ligula ornare aliquam. Vivamus nibh erat, suscipit non egestas vel, lacinia ut tortor. Ut mauris elit, mollis sit amet lobortis nec, semper lacinia dui. Aliquam accumsan semper felis sed interdum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla facilisi. Fusce dignissim rutrum gravida.
			</div>
		</div>
		<div id="backgroundPopup"></div>
		<div id="container">
			<div id="gamebuttons">
				<div class="content">
					<div class="buttons">
				{if isset($endgame)}
                                            <strong>Game has ended.</strong>{else}
                                                <div>
							<input type="button" id="vpnstart" value="Start VPN" {if not $allow_startvpn} disabled="disabled" {/if}/>
							<input type="button" id="vpnstop" value="Stop VPN" {if not $allow_stopvpn} disabled="disabled" {/if} />
							<input type="button" id="gstart" value="Start Game" {if not $allow_startgame} disabled="disabled" {/if} />
							<input type="button" id="gstop" value="Stop Game" {if not $allow_endgame} disabled="disabled" {/if} />
						</div>{/if}
					</div>
				</div>
			</div>
			<div id="contestants">
				<div class="content">
					<div class="header">
						<h1>Contestants</h1>
					</div>
					<div id="contestantinfo">
						<ul>
							<li>
								<div class="cban">Ban</div>
								<div class="csubnet">Subnet</div>
								<div class="cpoints">Points</div>
								<div class="cname">Team</div>
							</li>
						</ul>
						<form id="cform" method="GET">
							<ul class="centries">
		    {foreach from=$contestants item=contestant}<li class="{if $contestant->getSane() == false}insane{/if}{if $contestant->getOffline()} offline{/if}">
									<input type="radio" name="contestant" id="teamid_{$contestant->getId()}" value="{$contestant->getId()}" />
									<label for="teamid_{$contestant->getId()}">
										<div class="cban">{$contestant->getBantime()}</div>
										<div class="csubnet">{$contestant->getSubnet()}</div>
										<div class="cpoints">{$contestant->getPoints()}</div>
										<div class="cname">{$contestant->getTeamname()}</div>
                                                                                <div class="cextrainfo">
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel">VM IP:</div>
                                                                                        <div class="cextradata">{$contestant->getVm_ip()}</div></div>
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel">VPN Status:</div>
                                                                                        <div class="cextradata">{if $contestant->getVPNStatus()}Online{else}Offline{/if}</div></div>
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel"># VPN Conn:</div> {assign "num" $contestant->getNumVPNConn()}
                                                                                        <div class="cextradata">{if $num==false}-{else}{$num}{/if}</div></div>
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel">Last 5 Sanity:</div>
                                                                                        <div class="cextradata">{foreach $contestant->getSanityResults(5) as $sanres}
                                                                                        {$sanres->timestamp|date_format:"%H:%M:%S"} &nbsp; Port {$sanres->port} ({$sanres->service|escape:'html'}) reported by {$sanres->reporter}{if not $sanres@last}<br />{/if}{/foreach}
                                                                                        </div></div>

                                                                                        {if !is_null($contestant->getImage()) || !is_null($contestant->getTagline())}
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel">Image:</div>
                                                                                        <div class="cextradata"> <img src="images/{$contestant->getImage()}" width=64 height=64 /> </div></div>
                                                                                        <div style="float: none; position: relative;">
                                                                                            <div class="cextralabel">Tagline:</div>
                                                                                        <div class="cextradata"> {$contestant->getTagline()|escape}</div></div>
                                                                                        {/if}
                                                                                </div>
									</label>
								</li>{/foreach}
							</ul>
						</form>
					</div>
					<div class="buttons">
						<div>
							<input type="button" id="cadd" value="Add" />
							<input type="button" id="cedit" value="Edit" />
							<input type="button" id="cban" value="Ban" />
							<input type="button" id="cdelete" value="Delete" />
							{*<input type="button" id="cpoints" value="Points" disabled="disabled" />*}
							<input type="button" id="csanity" value="Sanity log" />
						</div>
					</div>
				</div>
			</div>
			<div id="announcements">
				<div class="content">
					<div class="header">
						<h1>Announcements</h1>
					</div>
					<form id="aform" method="GET" style="top:2em;">
						<div id="announcementdisplay">
							<ul class="collapsible">
		 {foreach from=$announcements item=announcement}<li>
									<input type="radio" name="announcement" value="{$announcement->id}" id="announcement{$announcement->id}"/>
									<label for="announcement{$announcement->id}">{$announcement->title}
                                                                                <span>{$announcement->timestamp|date_format:'%e/%m/%Y @ %H:%M'}</span>
										<div>{$announcement->content}</div>
									</label>
								</li>{/foreach}
							</ul>
						</div>
						<div class="buttons">
							<div>
								<input type="button" id="apost" value="Post" />
								<input type="button" id="aedit" value="Edit" />
								<input type="button" id="adelete" value="Delete" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
                <div id="notifybox"></div>
	</body>
</html>
