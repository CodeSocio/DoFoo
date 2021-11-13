 /*
  * DoFoo - A donation application
  * Copyright (C) 2021  CodeSocio
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  * Contact us at --> codesociodevs@gmail.com
  */

 package com.codesocio.dofoo.HelperClasses;

import java.lang.Exception;
import java.lang.Throwable;

public class CustomExceptions {
    public static class InvalidPasswordException extends Exception {
        public InvalidPasswordException(String message) {
            super(message);
        }
    };
    
    public static class InvalidPhoneException extends Exception {
        public InvalidPhoneException(String message) {
            super(message);
        }
    };
    
    public static class InvalidEmailException extends Exception {
        public InvalidEmailException(String message) {
            super(message);
        }
    };
}
