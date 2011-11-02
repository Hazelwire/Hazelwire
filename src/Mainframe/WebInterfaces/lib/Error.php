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
/**
 * Error is a class which is used by WebInterface in order to work with errors which have occurred.
 * An error is given a type, a message and whether the error which has occurred was fatal.
 *
 * @author Daniel
 */
class Error extends Exception{
    private $type;
    private $fatal;
    
    /**
     *
     * @param string $type The error type.
     * @param string $message A description of the error.
     * @param bool $fatal If this error should stop normal page processing.
     */
    function __construct($type, $message, $fatal = false){
        parent::__construct($message);
        $this->type = $type;
        $this->fatal = $fatal;
    }

    /**
     * Returns the type of this Error which was set when creating it
     * @return string The type of Error
     */
    public function getType(){
        return $this->type;
    }

    /**
     * Returns whether this Error is a fatal one, i.e. execution should be halted ASAP
     * @return boolean true if fatal, false otherwise
     */
    public function isFatal(){
        return $this->fatal;
    }
    
}

?>
