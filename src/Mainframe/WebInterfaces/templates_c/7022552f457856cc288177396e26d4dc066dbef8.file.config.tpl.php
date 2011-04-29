<?php /* Smarty version Smarty-3.0.6, created on 2011-04-28 18:20:18
         compiled from "D:\wampserver64\www\WebInterfaces\templates/config.tpl" */ ?>
<?php /*%%SmartyHeaderCode:290194db9afe2c6ada0-27580282%%*/if(!defined('SMARTY_DIR')) exit('no direct access allowed');
$_smarty_tpl->decodeProperties(array (
  'file_dependency' => 
  array (
    '7022552f457856cc288177396e26d4dc066dbef8' => 
    array (
      0 => 'D:\\wampserver64\\www\\WebInterfaces\\templates/config.tpl',
      1 => 1304013963,
      2 => 'file',
    ),
  ),
  'nocache_hash' => '290194db9afe2c6ada0-27580282',
  'function' => 
  array (
  ),
  'has_nocache_code' => false,
)); /*/%%SmartyHeaderCode%%*/?>
<!DOCTYPE html>
<html>
    <head>
        <title>Wargame Configuration | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <?php if ($_smarty_tpl->getVariable('db_created')->value==1){?>
            A database was just created with the filename <strong><?php echo $_smarty_tpl->getVariable('db_file_name')->value;?>
</strong>. This is relative to the root of this website (<strong><?php echo $_smarty_tpl->getVariable('site_path')->value;?>
</strong>).
        <?php }?>
        <h2>Config dingen bloat</h2>
        <?php if ($_smarty_tpl->getVariable('num_errors')->value>0){?>
        <span style="color: red;font-size:2em;border: 1px solid red"><?php echo $_smarty_tpl->getVariable('config_input_error')->value;?>
</span>
        <?php }?>
        <form method="POST" action="index.php">
            <div style="padding-bottom:0.2em;"> <label> Name </label> <input type="text" name="name" size="15"/> </div>
            <div style="padding-bottom:0.2em;"> <label> P2P interval (Min) </label> <input type="text" name="p2p_interval" size="4" value="10"/> </div>
            <div style="padding-bottom:0.2em;"> <label> S2P interval (Min) </label> <input type="text" name="s2p_interval" size="4" value="2" /> </div>
            <div> <label><!-- pseudo label --> </label>
                <input type="Submit" value="Next" /> </div>
        </form>
    </body>
</html>
