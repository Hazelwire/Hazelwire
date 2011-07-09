<a id="popupClose">x</a>
    <h1>Delete {if isset($contestant) && $contestant != false}{$contestant->getTeamname()}{else}null{/if}</h1>
    <div id="popupcontent">
				<div id="acform">
					<form method="POST">
						<div class="buttons">
							<div>
                                                                <input type="hidden" name="cid" value="{if isset($contestant) && $contestant != false}{$contestant->getId()}{else}-1{/if}" />
								<input type="button" id="okcdel" value="OK" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>