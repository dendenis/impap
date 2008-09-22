import socket, re 

def read_file(filename):
  f = open(filename, "r")  
  data = f.read()  
  f.close() 
  return filter(lambda str: str != "", data.splitlines())

def get_command_id(command):
  pattern = re.compile("([0-9]+) .")
  match = re.match(pattern, command)
  if match != None:
    return match.group(1)
  else:
    return None

if __name__ == "__main__":
  commands = read_file("script.txt")
  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

  try:
    s.connect(("imap.aim.com", 143))
    file = s.makefile('rb')
    answer = file.readline()
    print("s: %s" % answer) 

    for command in commands:
      print("c: %s" % command)
      command_id = get_command_id(command)
      s.send(command + "\r\n")
      if command_id != None:
        answer = file.readline()
        print("s: %s" % answer) 
        answer_id = get_command_id(answer) 

        while answer_id != command_id and not answer.startswith("+"):
          answer = file.readline()
          print("s: %s" % answer) 
          answer_id = get_command_id(answer) 
           
  finally:
    s.close()

