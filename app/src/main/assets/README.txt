IEEE oui.txt file: http://standards-oui.ieee.org/oui/oui.txt
Alternative: https://linuxnet.ca/ieee/oui.txt

Process this file using the following commands (using GNU tools):

grep '(hex)' oui.txt|sed -r 's/[[:space:]]+\(hex\)[[:space:]]+/|/g' | \
  sed -r 's/^([0-9A-F]+)-([0-9A-F]+)-([0-9A-F]+)(.+)/\1\2\3\4/g'| sort > oui_mfr.txt

Note: macOS uses a different version of sed, so the command-line parameters need to be changed.

oui_mfr.txt contains mappings between IEEE MAC addresses and manufacturers separated by the pipe character ('|').