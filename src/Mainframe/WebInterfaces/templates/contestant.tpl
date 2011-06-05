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
								<option value="flagtype">by Flag Type</option>
								<option value="warserver">by Warserver</option>
							</select>
						</div>
						<div id="flagdisplay">
							<ul class="collapsible">
								<li>1 point flag
									<div class="claimed">10.0.0.1</div>
									<div class="claimed">10.0.0.2</div>
									<div class="claimed">10.0.0.3</div>
								</li>
								<li>2 point flag
									<div class="unclaimed">10.0.0.1</div>
									<div class="unclaimed">10.0.0.2</div>
									<div class="unclaimed">10.0.0.3</div>
								</li>
								<li>3 point flag
									<div class="claimed">10.0.0.1</div>
									<div class="unclaimed">10.0.0.2</div>
									<div class="unclaimed">10.0.0.3</div>
								</li>
								<li>4 point flag
									<div class="unclaimed">10.0.0.1</div>
									<div class="claimed">10.0.0.2</div>
									<div class="claimed">10.0.0.3</div>
								</li>
								<li>5 point flag
									<div class="unclaimed">10.0.0.1</div>
									<div class="claimed">10.0.0.2</div>
									<div class="unclaimed">10.0.0.3</div>
								</li>
								<li>6 point flag
									<div class="claimed">10.0.0.1</div>
									<div class="unclaimed">10.0.0.2</div>
									<div class="claimed">10.0.0.3</div>
								</li>
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
								<li>Announcement 1 title
									<div>Announcement 1 content.<br />
										Sed eu nisl sapien, quis tincidunt purus. Donec id orci dui. Suspendisse potenti. Aliquam auctor neque id erat tristique facilisis. Cras nec felis elit. Curabitur ornare risus quis ipsum aliquet ut pretium nisi rhoncus. Donec placerat mi in purus pellentesque euismod.</div>
								</li>
								<li>Announcement 2 title
									<div>Announcement 1 content.<br />
										Sed eu nisl sapien, quis tincidunt purus. Donec id orci dui. Suspendisse potenti. Aliquam auctor neque id erat tristique facilisis. Cras nec felis elit. Curabitur ornare risus quis ipsum aliquet ut pretium nisi rhoncus. Donec placerat mi in purus pellentesque euismod.</div>
								</li>
								<li>Announcement 3 title
									<div>Announcement 1 content.<br />
										Sed eu nisl sapien, quis tincidunt purus. Donec id orci dui. Suspendisse potenti. Aliquam auctor neque id erat tristique facilisis. Cras nec felis elit. Curabitur ornare risus quis ipsum aliquet ut pretium nisi rhoncus. Donec placerat mi in purus pellentesque euismod.</div>
								</li>
								<li>Announcement 4 title
									<div>Announcement 1 content.<br />
										Sed eu nisl sapien, quis tincidunt purus. Donec id orci dui. Suspendisse potenti. Aliquam auctor neque id erat tristique facilisis. Cras nec felis elit. Curabitur ornare risus quis ipsum aliquet ut pretium nisi rhoncus. Donec placerat mi in purus pellentesque euismod.</div>
								</li>
								<li>Announcement 5 title
									<div>Announcement 1 content.<br />
										Sed eu nisl sapien, quis tincidunt purus. Donec id orci dui. Suspendisse potenti. Aliquam auctor neque id erat tristique facilisis. Cras nec felis elit. Curabitur ornare risus quis ipsum aliquet ut pretium nisi rhoncus. Donec placerat mi in purus pellentesque euismod.</div>
								</li>
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
	</body>
</html>
