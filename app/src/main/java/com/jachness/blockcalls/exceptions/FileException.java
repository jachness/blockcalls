/*
 * Copyright (C) 2017 Jonatan Cheiro Anriquez
 *
 * This file is part of Block Calls.
 *
 * Block Calls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Block Calls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Block Calls. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jachness.blockcalls.exceptions;

/**
 * Created by jachness on 19/12/2016.
 */

@SuppressWarnings("SameParameterValue")
public class FileException extends Exception {
    public static final int MEDIA_ERROR = 0;
    public static final int READ_FILE_ERROR = 1;
    public static final int WRITE_FILE_ERROR = 2;
    private final int errorCode;

    public FileException(int errorCode) {
        this.errorCode = errorCode;
    }

    public FileException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
