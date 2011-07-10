<a id="popupClose">x</a>
    <h1>Delete {if isset($announcement)}{$announcement->title|escape:'htmlall'}{/if}</h1>
    <div id="popupcontent">
            <div id="acform">
                    <form method="POST">
                            <input type="hidden" name="aid" value="{if isset($announcement)}{$announcement->id}{else}-1{/if}" />
                            <input type="hidden" name="submitted" value="true" />
                            <div class="buttons">
                                    <div>
                                            <input type="button" id="okadel" value="OK" />
                                            <input type="button" id="cancel" value="Cancel" />
                                    </div>
                            </div>
                    </form>
            </div>
    </div>
