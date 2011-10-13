<a id="popupClose">x</a>
			<h1>Add Contestant</h1>
			<div id="popupcontent">
                            <div id="acform">
                                <form method="POST"><input type="hidden" name="cadd" value="add" style="display:none;" />
                                        <div class="cforminput">
                                                <div class="cformlabel">Team name: </div>
                                                <div class="cformfield">
                                                        <input type="text" name="cname" title="teamname" class="defaultText" />
                                                </div>
                                                <div class="cformlabel">Subnet:</div>
                                                <div class="cformfield">
                                                        10.<input id="subnet" type="text" name="csubnet" value="2" title="#" class="defaultText" />.0.0/24
                                                </div>
                                                <div class="cformlabel">VM IP:</div>
                                                <div class="cformfield">
                                                        10.<span id="subinvmip">1</span>.<input type="text" name="cvmip" value="1" title="#" class="defaultText" />.1
                                                </div>
                                        </div>
                                        <div class="buttons">
                                                <div>
                                                        <input name="caddbutton" type="button" id="caddok" value="Add" />
                                                        <input type="button" id="cancel" value="Cancel" />
                                                </div>
                                        </div>
                                </form>
                            </div>
			</div>
