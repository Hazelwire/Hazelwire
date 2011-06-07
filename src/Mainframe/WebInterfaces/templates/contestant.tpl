<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="css/contestant.css" />
		<script src="js/jquery-1.6.1.min.js"></script>
		<script src="js/contestant.js"></script>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title>{$title} - Powered by Hazelwire</title>
	</head>
	<body>
		<div id="container">
			<div id="split">
				<div id="scoreboard">
					<div class="content">
						<div class="header">
							<h1>Scoreboard</h1>
						</div>
						<div id="scorecontainer">
							<ul class="scorelist">
								<li>
                                                                        <div class="floatleft">Team</div>
									<div>points</div>
								</li>
							</ul>
							<ol class="scorelist">
                                                            {foreach from=$contestants item=contestant}
								<li>
									<div class="floatleft">{$contestant->getTeamname()}</div>
									<div class="floatright">{$contestant->getPoints()}</div>
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
							<select>
								<option id="winsort" name="winsort" value="flagtype">by Flag Type</option>
								<option id="winsort" name="winsort" value="warserver">by Warserver</option>
							</select>
						</div>
						<div id="flagdisplay">
							<ul class="collapsible">
                                                            {foreach from=$flags item=flag}
								<li>{$flag->name}
                                                                        {foreach from=$flag->wins item=win}
                                                                        {if not $win->submitted}
									<div class="unclaimed">{$win->name}</div>
                                                                        {else}
                                                                        <div class="claimed">{$win->name}</div>
                                                                        {/if}
									{/foreach}
								</li>
                                                             {/foreach}
							</ul>	
						</div>
						<form id="flagform" action="index.php" method="POST">
							<div id="flaginput">
								<label class="txt-field">
									<input id="flaginputfield" name="flagcode" type="text" tabindex="1" />
								</label>
							</div>
							<div id="flagsubmission">
								<input id="flagsubmit" name="sub_flag" type="submit" value="Claim Flag" tabindex="2" />
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
								<li>{$announcement->title}
									<div>{$announcement->content}</div>
								</li>
                                                             {/foreach}
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div id="timeline">
				<div class="content">
					Javascript timeline goes here.
				</div>
			</div>
		</div>
            <div id="prepareblock" style="display:none;"></div>
	</body>
</html>
