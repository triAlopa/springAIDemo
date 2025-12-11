local keys=redis.call('keys',ARGV[1])

for i, key in ipairs(keys) do
    redis.call('del',key)
end
  return #keys