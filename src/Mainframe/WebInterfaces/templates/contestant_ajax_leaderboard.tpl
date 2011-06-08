<ol class="scorelist">
        {foreach from=$contestants item=contestant}
            <li>
                    <div class="floatleft">{$contestant->getTeamname()}</div>
                    <div class="floatright">{$contestant->getPoints()}</div>
            </li>
        {/foreach}
</ol>