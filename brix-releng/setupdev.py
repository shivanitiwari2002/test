#!/usr/bin/env python
# -*- coding: UTF8
"""
 Name:         setupdev.py
 Application:  System DevOps BrIX dev starter
 Module Name:  Setup prereqs for BrIX development
 Description:  Checks for queue, database, and simple endpoint servers.
               Must already have setup podman container images for the
               database and activemq servers named in setupdev.cfg
 (C) Copyright IBM Corporation 2025
 The Source code for this program is not published or otherwise
 divested of its trade secrets, irrespective of what has been
 deposited with the U.S. Copyright Office.
 Note to U.S. Government Users Restricted Rights:  Use,
 duplication or disclosure restricted by GSA ADP Schedule
 Contract with IBM Corp.
 Change Log:
 Flag Reason   Date     User Id   Description
 ---- -------- -------- --------  ------------------------------------------
               20250608 tollefso  New source file created.
 Additional notes about the Change Activity:
"""

#Requirements: podman, flyway, psql, python packages
#   There is a database called brixdb owned by user brixadmin
#   

import argparse
import configparser
from dotenv import load_dotenv
import logging
import socket
import subprocess
import sys
import os
from pythonjsonlogger import jsonlogger
from rich.console import Console
from rich.table import Table

PROG = 'setupdev'
LOGFILE_NAME = PROG + '.log'
DEFAULT_CONFIG_FILENAME = PROG + ".cfg"
DEFAULT_IMPORT_FILENAME = os.path.dirname(__file__) + '/../brix-app/src/main/resources/' + 'import' + '.sql'
VERSION = "1.0"

DEFAULT_CONFIG_SECTION = 'Default'
DEFAULT_ACTIVEMQ_NAME = 'brix_activemq'
DEFAULT_DATABASE_NAME = 'brix_postgres'

DEFAULT_HOST = 'localhost'
DEFAULT_TIMEOUT = 1

CONFIG_ACTIVEMQNAME_KEY = 'activemqname'
CONFIG_DATABASENAME_KEY = 'databasename'

STATUS_STOPPED = 'stopped'
STATUS_RUNNING = 'running'

ALL_DEPENDENCIES = 'all'
AVAILABLE_DEPENDENCIES = ['db', 'amq']
DATABASE_PORT = 5432
SERVICE_STATUS_STYLE = {STATUS_STOPPED:'[red on black]', STATUS_RUNNING:'[green on black]'}
# NOTE: Service name must match dependency name where there is a correspondance
SERVICES = {'db':           {'port':DATABASE_PORT, 'config_key':CONFIG_DATABASENAME_KEY, 'default_name':DEFAULT_DATABASE_NAME},
            'amq':          {'port':61616,'config_key':CONFIG_ACTIVEMQNAME_KEY, 'default_name':DEFAULT_ACTIVEMQ_NAME},
            'brix-app':     {'port':8080, 'config_key':'brixapp',      'default_name':'brix-app'},
            'brix-poller':  {'port':8084, 'config_key':'brixpoller',   'default_name':'brix-poller'},
            'brix-restapi': {'port':8088, 'config_key':'brixrest',     'default_name':'brix-restapi'},
            'brix-gui':     {'port':3000, 'config_key':'brixgui',      'default_name':'brix-gui'},
            'brix-receiver':{'port':8083, 'config_key':'brixreciever', 'default_name':'brix-receiver'},
            'simple1':      {'port':8091, 'config_key':'simple1',      'default_name':'simple1'},
            'simple2':      {'port':8092, 'config_key':'simple2',      'default_name':'simple2'},
           }

# Global :(
logger = logging.getLogger(__name__)
COPYRIGHT = "Licensed Materials - Property of IBM (C) COPYRIGHT 2025 All Rights Reserved."\
    " US Government Users restricted Rights - Use,"\
    "Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp."


def get_command_line_parser():
    """
    Sets up the command line parser.
    """
    parser = argparse.ArgumentParser(description=f'{PROG} {VERSION} \
Can setup BrIX dev. environment. \
Set the database password in environment variable "quarkus_datasource_password".\
 Reads other configuration values from {DEFAULT_CONFIG_FILENAME}. \
')

    subparsers = parser.add_subparsers(dest='command', help='Available commands')
    status_parser = subparsers.add_parser('status', help='Status of dependency')
    status_parser.add_argument('--service', help='Name of the dependency to get status('
        + ", " .join(AVAILABLE_DEPENDENCIES) +')',
        default=ALL_DEPENDENCIES)
    status_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    start_parser = subparsers.add_parser('start', help='Start dependency')
    start_parser.add_argument('--service', help='Name of the dependency to start('
        + ", " .join(AVAILABLE_DEPENDENCIES) +')', default=ALL_DEPENDENCIES)
    start_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    stop_parser = subparsers.add_parser('stop', help='Stop dependency')
    stop_parser.add_argument('--service', help='Name of the dependency to stop('
        + ", " .join(AVAILABLE_DEPENDENCIES) +')', default=ALL_DEPENDENCIES)
    stop_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    restart_parser = subparsers.add_parser('restart', help='Restart dependency')
    restart_parser.add_argument('--service', help='Name of the dependency to restart -stop'\
        ' followed by start-('\
        + ", " .join(AVAILABLE_DEPENDENCIES) +')',
        default=ALL_DEPENDENCIES)
    restart_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')

    #database related commands
    clean_parser = subparsers.add_parser('clean', help='Clean out tables and data from database')
    clean_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    migrate_parser = subparsers.add_parser('migrate', help='Migrate the database to the latest schema')
    migrate_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    import_parser = subparsers.add_parser('import', help='Imports data into the database')
    import_parser.add_argument('--importFile', metavar="FILE", required=False,
                        help='Name of file where the import data is read.\
                        This overrides the default "' +
                        DEFAULT_IMPORT_FILENAME + '"')
    import_parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')

    parser.add_argument('--configFile', metavar="FILE", required=False,
                        help='Name of file where the configuration is read.\
                        This overrides the default "' +
                        DEFAULT_CONFIG_FILENAME + '"')
    parser.add_argument('--jsonlogs', action='store_true', dest='jsonlogs',
                        help='Write logs in JSON format.')
    parser.add_argument('--debug', action='store_true', dest='writeDebug',
                        help='Write extra information to help debug this \
                        script.')
    parser.add_argument('--logtofile', action='store_true', dest='logToFile',
                        help='Log informational and debug messages to log \
                        file ' + LOGFILE_NAME)
    return parser

def get_logger(args):
    """
    Return the properly configured logger.
    """
    if args.writeDebug:
        new_logger = get_logger_with_debug(args.logToFile, args.jsonlogs)
    else:
        new_logger = get_logger_with_out_debug(args.logToFile, args.jsonlogs)

    new_logger.debug("command line arguments: %s", {str(args)})
    return new_logger

def get_logger_with_out_debug(log_to_file, jsonlogs):
    """
    Return a logger with INFO level.
    """
    log_file = sys.stdout
    if log_to_file:
        try:
            log_file = open(LOGFILE_NAME, 'a', encoding='utf-8')
        except IOError as e:
            print("I/O error(%s): %s", e.errno, e.strerror)

    handler = logging.StreamHandler(log_file)

    if jsonlogs:
        string_format = '%(asctime)%(levelname)%(message)%(name)%(lineno)'
        formatter = jsonlogger.JsonFormatter(string_format)
    else:
        formatter = logging.Formatter('%(asctime)s:%(levelname)s:%(message)s')

    handler.setFormatter(formatter)
    logger.addHandler(handler)
    logger.setLevel(level=logging.INFO)
    return logger

def get_logger_with_debug(log_to_file, jsonlogs):
    """
    Return a logger with DEBUG level.
    """
    logger_with_debug = get_logger_with_out_debug(log_to_file, jsonlogs)
    logger_with_debug.setLevel(level=logging.DEBUG)
    return logger_with_debug

def get_config_file_name(args):
    """
    Returns the name of the config file to use.
    """
    if args.configFile:
        file_name = args.configFile
        if not file_name.endswith(".cfg"):
            file_name = file_name + ".cfg"
    else:
        file_name = os.path.dirname(__file__) + "/" + DEFAULT_CONFIG_FILENAME
    return file_name

def get_config_value(config, section, key, default):
    """
    Returns value in config for given key.
    """
    value = default
    if section in config:
        if key in config[section]:
            value = config[section][key]
    return value

def get_config(args):
    """
    Load the config from file.
    """
    config_file_name = get_config_file_name(args)
    logger.debug(f"config_file_name={config_file_name}")
    config = configparser.ConfigParser()
    try:
        with open(config_file_name, 'r', encoding='utf-8') as config_file:
            config.read_file(config_file)
    except Exception as e:
        logger.error('"%s" is not a proper config file. %s',
            config_file_name, e)
        raise sys.exit()
    return config

def get_container_name_and_port(service, config):
    """
    Returns the name of the container and the service's port.
    """
    port = SERVICES[service]['port']
    config_key = SERVICES[service]['config_key']
    default_name = SERVICES[service]['default_name']
    container_name = get_config_value(config, DEFAULT_CONFIG_SECTION,
        config_key, default_name)
    return container_name, port

def is_container_running(container_name):
    """
    Uses podman to see if the named container has a running status.
    Returns a non zero exit code on failure.
    """
    command = "podman ps --filter name=" + container_name \
        + " --filter status=running | grep " + container_name
    return run_command(command)

def run_command(command, echo=False):
    """
    Returns a non zero exit code on failure.
    """
    logger.debug(command)
    result = subprocess.run([command], capture_output=True, shell=True, check=False)
    logger.debug(result.stdout)
    if echo == True:
        logger.info(result.stdout.decode("utf-8"))
    return result.returncode

def start_container_if_not_running(container_name):
    """
    Checks if the named container is running
        if not then attempts to start it
    """
    container_is_running = is_container_running(container_name)
    if container_is_running != 0 :
        logger.info("Container(%s) is not running", container_name)
        start_exit_code = start_container(container_name)
        if start_exit_code != 0:
            logger.info("Container(%s) NOT started.", container_name)
#            sys.exit(1)
        else:
            logger.info("Container(%s) started.", container_name)
    else:
        logger.info("Container(%s) is running", container_name)

def start_container(container_name):
    """
    Uses podman to start the named container.
    Returns a non zero exit code on failure.
    """
    command = "podman start " + container_name
    return run_command(command)

def status_container(container_name):
    """
    Returns the status of the container.
    """
    if is_container_running(container_name) == 0:
        return STATUS_RUNNING
    return STATUS_STOPPED

def status_port(host, port, timeout):
    """
    Returns a service status.
        running if port is being used
        stopped if port is not being used
    """
    logger.debug(f"host={host} port={port} timeout={timeout}")
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        result = sock.connect_ex((host, port))
        sock.close()
        logger.debug(f"socket connect to port; result={result}")
        if result == 0:
            return STATUS_RUNNING
    except Exception as e:
        logger.info(e)
        pass
    return STATUS_STOPPED

def stop_container(container_name):
    """
    Uses podman to stop the named container.
    Returns a non zero exit code on failure.
    """
    command = "podman stop " + container_name
    return run_command(command)


def main():
    """
    The starting point of the script.
    """
    global logger

    load_dotenv()
    parser = get_command_line_parser()
    args = parser.parse_args()
    logger = get_logger(args)
    config = get_config(args)

    if args.command == 'status':
        service = args.service
        logger.info(f"Status of dependency: {service}")
        console = Console()
        table = Table("Service")

        table.add_column("Status")
        table.add_column("Container Name")
        table.add_column("Port")

        for key in SERVICES:
            if service in (key, ALL_DEPENDENCIES):
                (container_name, port) = get_container_name_and_port(key, config)
                logger.debug(f"Checking status of service={key} on port={port}")
                status = status_container(container_name)
                port_status = status_port(DEFAULT_HOST, port, DEFAULT_TIMEOUT)
                port_status_style = SERVICE_STATUS_STYLE[port_status] + port_status + "[/]"
                if status != port_status:
                    logger.warning(f"port status({port_status}) doesn't match pod status(" +
                        f"{status}) for {container_name}")
                table.add_row(key, port_status_style, container_name, str(port))
        console.print(table)

    elif args.command == 'start':
        service = args.service
        logger.info(f"Starting dependency: " + service)
        for key in AVAILABLE_DEPENDENCIES:
            if service in (key, ALL_DEPENDENCIES):
                logger.debug(f"Starting matching dependency={key}")
                (container_name, port) = get_container_name_and_port(key, config)
                start_container_if_not_running(container_name)

    elif args.command == 'stop':
        service = args.service
        logger.info("Stopping dependency: " + service)
        for key in AVAILABLE_DEPENDENCIES:
            if service in (key, ALL_DEPENDENCIES):
                logger.debug(f"Stopping matching dependency={key}")
                (container_name, port) = get_container_name_and_port(key, config)
                stop_container(container_name)

    elif args.command == 'restart':
        service = args.service
        logger.info(f"Restarting dependency: " + service)
        for key in AVAILABLE_DEPENDENCIES:
            if service in (key, ALL_DEPENDENCIES):
                logger.debug(f"Restarting matching dependency={key}")
                (container_name, port) = get_container_name_and_port(key, config)
                logger.info("Stopping " + container_name)
                stop_container(container_name)
                logger.info("Starting " + container_name)
                start_container_if_not_running(container_name)

    elif args.command == 'clean':
        logger.info("Cleaning out tables and data from database")
        os.chdir(os.path.dirname(__file__) + "/../flyway")
        command = 'flyway -cleanDisabled="false" -user=brixadmin -password=' +\
            os.getenv('quarkus_datasource_password', 'brixadmin') +\
            " -url=jdbc:postgresql://" + DEFAULT_HOST + ":" + str(DATABASE_PORT) +"/brixdb clean"
        run_command(command, echo=True)

    elif args.command == 'migrate':
        logger.info("Migrating database to current schema")
        os.chdir(os.path.dirname(__file__) + "/../flyway")
        command = "flyway -user=brixadmin -password=" +\
            os.getenv('quarkus_datasource_password', 'brixadmin') +\
            " -url=jdbc:postgresql://" + DEFAULT_HOST + ":" + str(DATABASE_PORT) +"/brixdb migrate"
        run_command(command, echo=True)

    elif args.command == 'import':
        logger.info("Importing data into database")
        os.chdir(os.path.dirname(__file__) + "/../brix-app")
        os.environ['PGPASSWORD'] = os.getenv('quarkus_datasource_password', 'brixadmin')
        command ="psql --host=" + DEFAULT_HOST +" --port=" + str(DATABASE_PORT) + " --username=brixadmin brixdb < src/main/resources/import.sql"
        run_command(command, echo=True)
    else:
        # This handles cases where no subcommand is provided or help is requested
        parser.print_help()

if __name__ == "__main__":
    main()
