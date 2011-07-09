<a id="popupClose">x</a>
    <h1>Edit announcement</h1>
    <div id="popupcontent">
            <div id="acform">
                    <form method="POST">
                            <input type="hidden" name="aid" value="{if isset($announcement)}{$announcement->id}{else}-1{/if}" />
                            <input type="hidden" name="submitted" value="true" />
                            <div id="atitle">
                                    <input type="text" value="{if isset($announcement)}{$announcement->title|escape:'html'}{/if}" name="atitle" title="Announcement title" class="defaultText filldiv" />
                            </div>
                            <div id="abody">
                                    <textarea name="abody" title="Announcement body" class="defaultText filldiv">{if isset($announcement)}{$announcement->content}{/if}</textarea>
                            </div>
                            <div class="buttons">
                                    <div>
                                            <input type="button" id="okaedit" value="Save" />
                                            <input type="button" id="cancel" value="Cancel" />
                                    </div>
                            </div>
                    </form>
            </div>
    </div>