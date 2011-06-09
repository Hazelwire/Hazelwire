<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>{$title} - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/textfield.js"></script>
	</head>
	<body>
		<div id="container">
			<div class="content">
				<div id="acform">
					<div class="header">
						<h1>Ban {$contestant->getTeamname()}</h1>
					</div>
					<form><input type="hidden" value="{$contestant->getId()}" />
						for <input type="text" name="cbantime" title="#" class="defaultText" /> minutes.
						<div class="buttons">
							<div>
								<input type="button" id="okcban" value="OK" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
