<a id="popupClose">x</a>
    <h1>Ban {if isset($contestant) && $contestant  != false}{$contestant->getTeamname()}{/if}</h1>
    <div id="popupcontent">
                                <div id="acform">
                                        <form method="POST"><input type="hidden" name="cid" value="{{if isset($contestant) && $contestant != false}}{$contestant->getId()}{/if}" />
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

