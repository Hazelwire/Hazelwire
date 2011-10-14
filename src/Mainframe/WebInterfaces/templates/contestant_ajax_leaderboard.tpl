<ol class="scorelist">
        {foreach from=$contestants item=contestant}
            <li>
                    <span>{$contestant->getTeamname()}</span>
                    <span>{$contestant->getPoints()}</span>
            </li>
        {/foreach}
</ol>
