<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>{$title} - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/collapse.js"></script>
	</head>
	<body>
		<div id="container">
			<div id="gamebuttons">
				<div class="content">
					<div class="buttons">
						<div>
							<input type="button" id="vpnstart" value="Start VPN" />
							<input type="button" id="vpnstop" value="Stop VPN" disabled="disabled" />
							<input type="button" id="gstart" value="Start Game" disabled="disabled" />
							<input type="button" id="gstop" value="Stop Game" disabled="disabled" />
						</div>
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
						<form id="cform">
							<ul class="centries">
		    {foreach from=$contestants item=contestant}<li class="{if $contestant->getSane() == false}insane{/if}{if $contestant->getOffline()} offline{/if}">
									<input type="radio" name="contestant" id="teamid_{$contestant->getId()}" value="{$contestant->getId()}" />
									<label for="teamid_{$contestant->getId()}">
										<div class="cban">{$contestant->getBantime()}</div>
										<div class="csubnet">{$contestant->getSubnet()}</div>
										<div class="cpoints">{$contestant->getPoints()}</div>
										<div class="cname">{$contestant->getTeamname()}</div>
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
							<input type="button" id="cpoints" value="Points" disabled="disabled" />
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
					<form id="aform">
						<div id="announcementdisplay">
							<ul class="collapsible">
		 {foreach from=$announcements item=announcement}<li>
									<input type="radio" name="announcement" value="{$announcement->id}" id="announcement{$announcement->id}"/>
									<label for="announcement{$announcement->id}">{$announcement->title}
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
	</body>
</html>
