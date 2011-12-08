<ol class="scorelist">{counter start=0 skip=1 assign="count"}
        {foreach from=$contestants item=contestant}
            <li>
                    <div class="title-bar">
                    <span id='{counter}{$count}'>{$contestant->getTeamname()}</span>
                    <span>{$contestant->getPoints()}</span>
                    </div>
                    <div class="cextrainfo">
                        {if !is_null($contestant->getImage()) && !is_null($contestant->getTagline())}
                                <div class="image" style="background: #000 url('images/{$contestant->getImage()}') no-repeat right top;"> &nbsp </div>
                                <div class="tagline"> {$contestant->getTagline()|escape} </div>
                        {elseif $id == $contestant->getId()}
                                <form method="POST" action="index.php" enctype="multipart/form-data">
                                    <div class="tagline" style="background: #aeaeae" title="Max 500kb. JPG, GIF or PNG."> Image: <input id="fileinput" type="file" name="img"/> </div>
                                    <div class="tagline" title="Max 140 character. No markup."> Tagline: <input type="text" name="tag" /> </div>
                                    <div class="tagline"> <input type="submit" value="Submit" /> </div>
                                </form>
                        {else}
                                <div class="image" style="background: #aeaeae"> None </div>
                                <div class="tagline"> None </div>
                        {/if}
                    </div>
            </li>
        {/foreach}
</ol>