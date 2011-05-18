<table width="290">
    <tr>
        <td width="30">Rank</td>
        <td width="160">Name</td>
        <td width="100">Points</td>{counter start=0 skip=1 assign="rank"}
    </tr>{foreach from=$contestants item=contestant}
    <tr>{counter}
        <td width="30">{$rank}</td>
        <td width="160">{$contestant->getTeamname()}</td>
        <td width="100">{$contestant->getPoints()} points</td>
    </tr>{/foreach}
</table>