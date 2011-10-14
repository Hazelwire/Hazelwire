<ul class="collapsible">{foreach from=$announcements item=announcement}
    <li>
        <input type="radio" name="announcement" value="{$announcement->id}" id="announcement{$announcement->id}"/>
        <label for="announcement{$announcement->id}">{$announcement->title}
            <span>{$announcement->timestamp|date_format:'%e/%m/%Y @ %H:%M'}</span>
            <div>{$announcement->content}</div>
        </label>
    </li>{/foreach}
</ul>
