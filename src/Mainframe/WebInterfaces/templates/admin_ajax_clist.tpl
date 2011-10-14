<ul class="centries">{foreach from=$contestants item=contestant}
    <li class="{if $contestant->getSane() == false}insane{/if}{if $contestant->getOffline()} offline{/if}">
        <input type="radio" name="contestant" id="teamid_{$contestant->getId()}" value="{$contestant->getId()}" />
        <label for="teamid_{$contestant->getId()}">
            <div class="cban">{$contestant->getBantime()}</div>
            <div class="csubnet">{$contestant->getSubnet()}</div>
            <div class="cpoints">{$contestant->getPoints()}</div>
            <div class="cname">{$contestant->getTeamname()}</div>
            <div class="cextrainfo">
                <div class="cextralabel">VM IP:</div>
                <div class="cextradata">{$contestant->getVm_ip()}</div>
                <div class="cextralabel">VPN Status:</div>
                <div class="cextradata">{if $contestant->getVPNStatus()}Online{else}Offline{/if}</div>
                <div class="cextralabel"># VPN Conn:</div> {assign "num" $contestant->getNumVPNConn()}
                <div class="cextradata">{if $num==false}-{else}{$num}{/if}</div>
                <div class="cextralabel">Last 5 Sanity:</div>
                <div class="cextradata">{foreach $contestant->getSanityResults(5) as $sanres}
                {$sanres->timestamp|date_format:"%H:%M:%S"} &nbsp; Port {$sanres->port} ({$sanres->service|escape:'html'}) reported by {$sanres->reporter}{if not $sanres@last}<br />{/if}{/foreach}
                </div>
            </div>
        </label>
    </li>{/foreach}
</ul>
