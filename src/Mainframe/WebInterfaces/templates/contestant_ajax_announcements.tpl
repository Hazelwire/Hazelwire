<ul class="collapsible">
        {foreach from=$announcements item=announcement}
        <li>
                <input type="radio" name="announcement" id="announcement_{$announcement->id}"/>
                <label for="announcement_{$announcement->id}">{$announcement->title}
                        <span>{$announcement->timestamp|date_format:'%e/%m/%Y @ %H:%M'}</span>
                        <div>{$announcement->content}</div>
                </label>
        </li>
        {/foreach}
</ul>
