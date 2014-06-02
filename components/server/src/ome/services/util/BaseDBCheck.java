/*
 * Copyright (C) 2014 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package ome.services.util;

import ome.util.SqlAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility methods for checking if startup-time database adjustments have yet been performed.
 * @author m.t.b.carroll@dundee.ac.uk
 * @since 5.0.2
 */
abstract class BaseDBCheck {
    private static final Logger log = LoggerFactory.getLogger(BaseDBCheck.class);

    /** executor useful for performing database adjustments */
    protected final Executor executor;

    private final String configKey = "DB check " + getClass().getSimpleName();
    private final String configValue = getCheckDone();

    /**
     * @param executor executor to use for configuration map check
     */
    protected BaseDBCheck(Executor executor) {
        this.executor = executor;
    }

    /**
     * @return if the database adjustment is not yet performed
     */
    private boolean isCheckRequired() {
        return (Boolean) executor.executeSql(
                new Executor.SimpleSqlWork(this, "BaseDBCheck") {
                    @Transactional(readOnly = true)
                    public Boolean doWork(SqlAction sql) {
                        return !configValue.equals(sql.configValue(configKey));
                    }
                });
    }

    /**
     * The database adjustment is now performed.
     * Hereafter {@link #isCheckRequired()} should return {@code false}.
     */
    private void checkIsDone() {
        executor.executeSql(
                new Executor.SimpleSqlWork(this, "BaseDBCheck") {
                    @Transactional(readOnly = false)
                    public Object doWork(SqlAction sql) {
                        sql.updateOrInsertConfigValue(configKey, configValue);
                        return null;
                    }
                });
    }

    /**
     * Do the database adjustment only if not already performed.
     */
    public void start() {
        if (isCheckRequired()) {
            doCheck();
            checkIsDone();
            log.info("performed " + configKey + ": " + configValue);
        } else if (log.isDebugEnabled()) {
            log.debug("skipped " + configKey);
        }
    }

    /**
     * Do the database adjustment.
     */
    protected abstract void doCheck();

    /**
     * @return a string identifying that the check is done, never {@code null}
     */
    protected String getCheckDone() {
        return "done";
    }
}