<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>Configure Hazelwire Wargame</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
		<link rel="stylesheet" type="text/css" href="css/config.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/collapse.js"></script>
		<script type="text/javascript" src="js/textfield.js"></script>
                <script type="text/javascript">
	       {literal}$(document).ready(function() {
				$('#fileinput').fadeTo(1,0);
				$('#fileinput').change(function(event){$('#browseField').val($('#fileinput').val())});
				$('#browseButton').click(function(event){
					$('#fileinput').click();
				});
                                $('#browseField').click(function(event){
					$('#fileinput').click();
				});{/literal}
                                {if $db_created == 1}
                                addNotification("A database was just created with the filename <strong>{$db_file_name}</strong>. This is relative to the root of this website (<strong>{$site_path}</strong>).","notifyneutral");
                                {/if}
                                {if $num_errors >0}
                                addNotification("{$config_input_error}","notifybad");
                                {/if}
		{literal}});{/literal}
		</script>
	</head>
	<body>
		<div id="container">
			<div class="content">
				<div class="header">
					<h1>Configure wargame</h1>
				</div>
				<div id="configentries">
					<form method="POST" action="index.php" enctype="multipart/form-data">
                                                <input type="hidden" name="configsubmit" value="true" />
						<ul>
							<li>
								<input type="text" name="name" id="setting0000" title="Hazelwire Wargame" class="defaultText" />
								<label for="setting0000">Name</label>
							</li>
							<li>
								<input type="text" name="p2p_interval" id="setting0001" title="10" class="defaultText" />
								<label for="setting0001">Peer2Peer Sanity Check interval (in minutes)</label>
							</li>
							<li>
								<input type="text" name="s2p_interval" id="setting0010" title="5" class="defaultText" />
								<label for="setting0010">Server2Peer Sanity Check interval (in minutes)</label>
							</li>
							<li>
								<input type="text" name="server_ip" id="setting0011" title="" class="defaultText" />
								<label for="setting0011">Server's IP</label>
							</li>
							<li>
								<input type="text" name="points_decay_mod" id="setting0100" title="0.25" class="defaultText" />
								<label for="setting0100">Points Decay Modifier</label>
							</li>
							<li>
								<input type="text" name="points_min" id="setting0101" title="1" class="defaultText" />
								<label for="setting0101">Minimum Points per Score:</label>
							</li>
							<li>
								<input type="text" name="point_penalty_mod" id="setting0110" title="0.5" class="defaultText" />
								<label for="setting0110">Points Penalty Modifier:</label>
							</li>
							<li>
								<input id="fileinput" type="file" name="manifest"/>
								<input type="text" id="browseField" title="Manifest File" style="z-index:1;"/>
								<!--<input type="button" id="browseButton" value="Browse..." style="height:1.8em;z-index:1;"/>-->
								<label for="setting0111">Manifest file</label>
							</li>
						</ul>
					</form>
				</div>
				<div class="buttons">
					<div>
						<input type="button" id="confok" value="OK" />
						{*<input type="button" id="confback" value="Back" />*}
					</div>
				</div>
			</div>
		</div>
		<div id="notifybox"></div>
	</body>
</html>