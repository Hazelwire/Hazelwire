<table width="290">
    <tr>
        <td width="30">Rank</td>
        <td width="160">Name</td>
        <td width="100">Points</td>
    </tr>{foreach from=$contestants item=contestant key=index name=count}
    <tr>
        <td width="30">{$smarty.foreach.count.index}</td>
        <td width="160">{$contestant->getTeamname()}</td>
        <td width="100">{$contestant->getPoints()} points</td>
    </tr>{/foreach}
</table>