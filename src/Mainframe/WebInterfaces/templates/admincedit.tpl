<a id="popupClose">x</a>
    <h1>Edit {if isset($contestant)}{$contestant->getTeamname()}{/if}'s info</h1>
    <div id="popupcontent">
                <div id="acform">
                        <form method="POST"><input type="hidden" name="cid" value="{if isset($contestant)}{$contestant->getId()}{/if}" /><input type="hidden" name="cedit" value="save" />
                            <div class="cforminput">
                                    <div class="cformlabel">Team name: </div>
                                    <div class="cformfield">
                                            <input type="text" value="{if isset($contestant)}{$contestant->getTeamname()}{/if}" name="cname" title="teamname" class="defaultText" />
                                    </div>
                                    <div class="cformlabel">Subnet:</div>
                                    <div class="cformfield">
                                            10.<input id="subnet" type="text" value="{if isset($subnet)}{$subnet}{/if}" name="csubnet" value="2" title="#" class="defaultText" />.0.0/24
                                    </div>
                                    <div class="cformlabel">VM IP:</div>
                                    <div class="cformfield">
                                            10.<span id="subinvmip">1</span>.<input type="text" value="{if isset($vmip)}{$vmip}{/if}" name="cvmip" value="1" title="#" class="defaultText" />.1
                                    </div>
                            </div>
                            <div class="buttons">
                                    <div>
                                            <input type="button" id="okcedit" value="Save" />
                                            <input type="button" id="cancel" value="Cancel" />
                                            <input type="button" id="keys" value="Keys" />
                                            <input type="button" id="sanity" value="Sanity check" />
                                    </div>
                            </div>
                    </form>
            </div>
    </div>
