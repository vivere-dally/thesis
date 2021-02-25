import sys
import json


def update_package_json_file(args):
    """
        Updates the version field in a package.json file.
    Args:
        args ([list]): A list with the arguments recevied from the command line. args should contain 2 elements: the file_path and the new version.

    Returns:
        [tuple]: a tuple where the first value is the exit code and the second value is a custom message that can be printed.
    """
    
    if len(args) != 2:
        message = 'Usage: [update_package_json_file] [file_path] [version]'
        return (1, message)

    file_path = args[0]
    version = args[1]
    data = None
    with open(file_path, 'r') as fin:
        data = json.load(fin)

    if not data:
        return (1, 'Bad input file.')

    data['version'] = version
    with open(file_path, 'w') as fout:
        json.dump(data, fout, indent=2)
        fout.write('\n')
        fout.flush()

    return (0, None)


if __name__ == '__main__':
    if len(sys.argv) <= 1:
        print('Usage: [<path>/Utils.py] [function_to_invoke] [function_args_space_separated]')
        exit(1)

    exit_code, message = locals()[sys.argv[1]](sys.argv[2:])
    if message:
        print(str(message))

    exit(exit_code)
