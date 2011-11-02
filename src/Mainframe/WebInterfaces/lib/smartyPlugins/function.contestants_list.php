<?php
/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

function smarty_function_contestants_list($params, $template)
{
        /* @var $interface WebInterface */
      global $interface;
      $db = &$interface->database; /* @var $db PDO */
      $smarty = &$interface->getSmarty();
      $tpl = $smarty->createTemplate("leaderboard.tpl");                        /* @var $tpl Smarty_Internal_Template */

      // flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
      // flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
      // teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
      // scores (team_id INTEGER, flag TEXT, timestamp INTEGER, points INTEGER);
      $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                         "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                         "GROUP BY teams.id ORDER BY sum(scores.points) DESC;");

      $contestants = array();
      while (($res = $q->fetch()) !== false){
          $c = Contestant::getById($res['team_id'], $db);
          array_push($contestants, $c);
      }
      $tpl->assign("contestants",$contestants);
      return $tpl->fetch();
}

?>
